import json
import pkgutil, sys, urlparse, os
# Import flask and template operators
import urllib

from flask import Flask, render_template, request, redirect, Response, url_for
# Import SQLAlchemy
from flask.ext.sqlalchemy import SQLAlchemy
from flask.templating import DispatchingJinjaLoader
from flask.globals import _request_ctx_stack

class ModifiedLoader(DispatchingJinjaLoader):
    def _iter_loaders(self, template):
        bp = _request_ctx_stack.top.request.blueprint
        if bp is not None and bp in self.app.blueprints:
            loader = self.app.blueprints[bp].jinja_loader
            if loader is not None:
                yield loader, template

        loader = self.app.jinja_loader
        if loader is not None:
            yield loader, template

# Define the WSGI application object
app = Flask(__name__)

# Configurations
app.config.from_object('config')
app.jinja_options = Flask.jinja_options.copy()
app.jinja_options['loader'] = ModifiedLoader(app)

# Define the database object which is imported
# by modules and controllers
db = SQLAlchemy(app)

# Sample HTTP error handling
@app.errorhandler(404)
def not_found(error):
    return render_template('404.html'), 404

# Load all modules
sys.path.append('app/modules')
for importer, package_name, _ in pkgutil.iter_modules(['app/modules']):
    importer.find_module(package_name).load_module(package_name) 

# Register modules
from flask import Blueprint
for mod_cls in Blueprint.__subclasses__():
    if str(mod_cls.__module__).startswith('mod'):
        mod = mod_cls()
        app.register_blueprint(mod)


# Add default API route list ==================================================
blueprint_rules = {}
for rule in app.url_map.iter_rules():
    try:
        paths = [x for x in rule.rule.split("/") if x]
        blueprint_rules.setdefault(paths[0], [])
        blueprint_rules[paths[0]].append(rule)
    except :
        pass

@app.route('/api/')
@app.route('/<blueprint>/api/')
def api_index(blueprint=None):
    if blueprint:
        selected_blueprint_rules = blueprint_rules[str(blueprint)]
    else:
        selected_blueprint_rules = []
        for k, v in blueprint_rules.items():
            selected_blueprint_rules += v

    rules = []
    for rule in selected_blueprint_rules:
        #print(rule)
        options = {}
        for arg in rule.arguments:
            options[arg] = "[{0}]".format(arg)

        methods = ','.join(rule.methods)
        url = url_for(rule.endpoint, **options)
        url = urllib.unquote(url)

        # Swap module and api path
        paths = [x for x in rule.rule.split("/") if x]
        if len(paths) > 1:
            if paths[1].lower() == 'api' or paths[1].lower() == 'admin':
                tmp_path = paths[0]
                paths[0] = paths[1]
                paths[1] = tmp_path

                url = '/' + '/'.join(paths) + '/'

        new_rule = {'endpoint' : rule.endpoint, 'methods' : methods, 'url' :
            url}
        if new_rule not in rules:
            rules.append(new_rule)

    # Sort list by endpoint
    rules = sorted(rules, key=lambda k: k['url'])
    return Response(json.dumps(rules), mimetype='application/json')

# Swap module and api path ====================================================
for rule in app.url_map.iter_rules():
    paths = [x for x in rule.rule.split("/") if x]
    if len(paths) > 1:
        if paths[1].lower() == 'api' or paths[1].lower() == 'admin':
            tmp_path = paths[0]
            paths[0] = paths[1]
            paths[1] = tmp_path

            new_rule = '/' + '/'.join(paths) + '/'
            app.add_url_rule(new_rule, rule.endpoint, methods=rule.methods)

# Build the database:
# This will create the database file using SQLAlchemy
db.create_all()

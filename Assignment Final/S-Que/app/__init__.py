import pkgutil
import sys
# Import flask and template operators
from flask import Flask, render_template
# Import SQLAlchemy
from flask.ext.sqlalchemy import SQLAlchemy

from flask.templating import DispatchingJinjaLoader
from flask.globals import _request_ctx_stack
from apscheduler.schedulers.background import BackgroundScheduler

# Init logging
#import logging
#logging.basicConfig()

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

# Define the database object which is imported by modules and controllers
db = SQLAlchemy(app)

# Define cron
cron = BackgroundScheduler(daemon=True)

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

# Swap module and api path
for rule in app.url_map.iter_rules():
    paths = [x for x in rule.rule.split("/") if x]
    if len(paths) > 1:
        if paths[1].lower() != 'api':
            continue
        
        tmp_path = paths[0]
        paths[0] = paths[1]
        paths[1] = tmp_path

        new_rule = '/' + '/'.join(paths) + '/'
        app.add_url_rule(new_rule, rule.endpoint, methods=rule.methods)

# Sample HTTP error handling
@app.errorhandler(404)
def not_found(error):
    return render_template('404.html'), 404

# Build the database:
# This will create the database file using SQLAlchemy
db.create_all()

# Run all cron
cron.start()

import pkgutil, sys, urlparse, os
# Import flask and template operators
from flask import Flask, render_template, request, redirect
# Import SQLAlchemy
from flask.ext.sqlalchemy import SQLAlchemy

# Define the WSGI application object
app = Flask(__name__)
# Configurations
app.config.from_object('config')

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

# Swap module and api path
for rule in app.url_map.iter_rules():
    paths = [x for x in rule.rule.split("/") if x]
    if len(paths) > 0 and paths[1].lower() == 'api':
        tmp_path = paths[0]
        paths[0] = paths[1]
        paths[1] = tmp_path

        new_rule = '/' + '/'.join(paths) + '/'
        app.add_url_rule(new_rule, rule.endpoint)

# Build the database:
# This will create the database file using SQLAlchemy
db.create_all()

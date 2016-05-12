import sys, os
sys.path.insert(0, '/var/www/bandung-service-catalog-wsgi')
sys.path.insert(0, '/var/www/bandung-service-catalog-wsgi/env/lib/python2.7/site-packages')

os.chdir(os.path.dirname(os.path.realpath(__file__)))

from start_wsgi import app as application

from werkzeug.debug import DebuggedApplication
application = DebuggedApplication(application, True)
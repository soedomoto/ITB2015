# Listen address
HOST = 'localhost'
PORT = 8080

BASE_URL = 'http://{}:{}'.format(HOST, PORT)

# Statement for enabling the development environment
DEBUG = True

# Set server name
# import socket
# SERVER_NAME = '%s:%s' % (socket.gethostbyname(socket.gethostname()), 8080)

# Define the application directory
import os
BASE_DIR = os.path.abspath(os.path.dirname(__file__))

# Define template directory
TEMPLATE_DIR = os.path.join(BASE_DIR, 'app', 'templates')

# Define the database - we are working with
# SQLite for this example
SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(BASE_DIR, 'app.db')
SQLALCHEMY_TRACK_MODIFICATIONS = True
DATABASE_CONNECT_OPTIONS = {}

# Application threads. A common general assumption is
# using 2 per available processor cores - to handle
# incoming requests using one and performing background
# operations using the other.
THREADS_PER_PAGE = 2

# Enable protection agains *Cross-site Request Forgery (CSRF)*
CSRF_ENABLED     = True

# Use a secure, unique and absolutely secret key for
# signing the data. 
CSRF_SESSION_KEY = "secret"

# Secret key for signing cookies
SECRET_KEY = "secret"

'''
=============================================================================
CRON JOB
=============================================================================
All time is in minutes
'''
SERVICE_TIME_CHECK_INTERVAL                 = 1
ARRIVAL_TIME_CHECK_INTERVAL                 = 1
SERVICE_TIME_PREDICTION_CHECK_INTERVAL      = 1

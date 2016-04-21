from app import app

# Import logging
import logging
from logging.handlers import RotatingFileHandler

from daemonize import Daemonize
pid = "app.pid"

# Run a Werkzeug server.
def start_server():
    logFormatStr = '[%(asctime)s] {%(pathname)s:%(lineno)d} %(levelname)s - %(message)s'
    logging.basicConfig(format = logFormatStr, filename = "app.log", level=logging.DEBUG)
    formatter = logging.Formatter(logFormatStr,'%m-%d %H:%M:%S')
    fileHandler = logging.FileHandler("app.log")
    fileHandler.setLevel(logging.DEBUG)
    fileHandler.setFormatter(formatter)
    streamHandler = logging.StreamHandler()
    streamHandler.setLevel(logging.DEBUG)
    streamHandler.setFormatter(formatter)
    app.logger.addHandler(fileHandler)
    app.logger.addHandler(streamHandler)
    app.logger.info("Logging is set up.")
    
    app.run(host='0.0.0.0', port=8080, debug=True)

if __name__ == '__main__':
    # daemon = Daemonize(app="bandung_service_catalog", pid=pid, action=start_server)
    # daemon.start()
    
    start_server()
        
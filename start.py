from app import app
import os
# Import logging
import logging
from logging.handlers import RotatingFileHandler

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
    
    app.run(host='0.0.0.0', port=app.config.get("PORT", 8080), debug=True, threaded=True)

if __name__ == '__main__':
    start_server()
        
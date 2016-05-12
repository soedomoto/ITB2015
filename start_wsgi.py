import logging
from logging.handlers import RotatingFileHandler

from app import app
app.debug = True

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

if __name__ == '__main__':
    app.run()
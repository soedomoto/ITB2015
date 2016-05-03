from app import app

# Run a Werkzeug server.
def start_server():
    app.run(host='0.0.0.0', port=8080, debug=True, threaded=True)

if __name__ == '__main__':
    start_server()
        
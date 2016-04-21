from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify
        
class Home(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'home', __name__, url_prefix='/', template_folder='views')
        self.web()
        self.api()
    
    def web(self):
        @self.route('/', methods=['GET', 'POST'])
        def index():
            return render_template("index.html")
        
    def api(self):
        pass
            
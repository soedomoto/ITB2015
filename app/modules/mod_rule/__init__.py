from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response
import json, urllib
from app import app
        
class Rule(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'rule', __name__, url_prefix='/rule')
        self.web()
        self.api()
    
    def web(self):
        pass
        
    def api(self):
        @self.route('/api/route/', methods=['GET'])
        @self.route('/api/route/filter/<filter>/', methods=['GET'])
        def api_prov_list(filter=None):
            rules = []
            for rule in app.url_map.iter_rules():
                options = {}
                for arg in rule.arguments:
                    options[arg] = "[{0}]".format(arg)
                    
                methods = ','.join(rule.methods)                
                url = url_for(rule.endpoint, **options)
                url = urllib.unquote(url)
                
                if filter and filter not in url:
                    continue
                
                # Swap module and api path
                paths = [x for x in url.split("/") if x]
                if len(paths) > 0 and paths[1].lower() == 'api':
                    tmp_path = paths[0]
                    paths[0] = paths[1]
                    paths[1] = tmp_path

                    url = '/' + '/'.join(paths) + '/'
                
                rules.append({
                    'endpoint' : rule.endpoint, 'methods' : methods, 'url' : url
                })
            
            # Sort list by endpoint
            rules = sorted(rules, key=lambda k: k['url']) 
            return Response(json.dumps(rules), mimetype='application/json')
            
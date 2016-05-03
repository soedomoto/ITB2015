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
                methods = ','.join(rule.methods)
                # Swap module and api path
                paths = [x for x in rule.rule.split("/") if x]
                url = '/' + '/'.join(paths) + '/'
                if len(paths) > 1:
                    if paths[1].lower() == 'api':
                        tmp_path = paths[0]
                        paths[0] = paths[1]
                        paths[1] = tmp_path

                        url = '/' + '/'.join(paths) + '/'

                if filter and filter not in url:
                    continue

                new_rule = {'endpoint' : rule.endpoint, 'methods' : methods, 'url' : url}
                if new_rule not in rules:
                    rules.append(new_rule)
            
            # Sort list by endpoint
            rules = sorted(rules, key=lambda k: k['url']) 
            return Response(json.dumps(rules), mimetype='application/json')
            
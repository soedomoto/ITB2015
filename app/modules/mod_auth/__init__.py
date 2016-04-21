from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify
from forms import LoginForm
from models import User
        
class Authentication(Blueprint):
    def __init__(self):
        # Define the blueprint: 'auth', set its url prefix: app.url/auth
        Blueprint.__init__(self, 'auth', __name__, url_prefix='/auth', template_folder='templates')
        
        # Set the route and accepted methods
        @self.route('/signin/', methods=['GET', 'POST'])
        def signin():

            # If sign in form is submitted
            form = LoginForm(request.form)

            # Verify the sign in form
            if form.validate_on_submit():

                user = User.query.filter_by(email=form.email.data).first()

                if user and check_password_hash(user.password, form.password.data):

                    session['user_id'] = user.id

                    flash('Welcome %s' % user.name)

                    return redirect(url_for('auth.home'))

                flash('Wrong email or password', 'error-message')

            return render_template("signin.html", form=form)
            
#!/bin/bash

#You may need to set up heroku as a remote:
# git remote -v
# heroku git:remote -a my-heroku-slug
# git remote -v

git push heroku master

#print current time
echo ''
date


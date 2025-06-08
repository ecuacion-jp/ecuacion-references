#!/bin/bash

export PROJ_NAME=ecuacion-references/ecuacion-splib-web-project-template

../../../ecuacion-internal-utils/ecuacion-util-bootstrap/script/command.sh `pwd`/custom.scss

mv ../../../ecuacion-internal-utils/ecuacion-util-bootstrap/script/bootstrap* .
cp bootstrap* ../../../${PROJ_NAME}/src/main/resources/static/css/
rm bootstrap*

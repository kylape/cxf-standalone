#!/bin/bash

curl -s -H "Content-Type: text/xml" -d @request.xml http://localhost:8080/hello/hello | xmllint --format - 

#!/bin/bash

kubectl apply -f account.yaml
kubectl apply -f budget.yaml
kubectl apply -f config.yaml
kubectl create -f headless-service.yaml --save-config
kubectl create -f deployment.yaml --save-config

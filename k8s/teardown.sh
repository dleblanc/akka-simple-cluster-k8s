#!/bin/bash

kubectl delete -f deployment.yaml
kubectl delete -f account.yaml
kubectl delete -f budget.yaml
kubectl delete -f config.yaml

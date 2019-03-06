# Simple Akka Cluster on GKE kubernetes cluster

This has been tailored from the upstream [Akka-Simple-Cluster-K8s](https://github.com/softwaremill/akka-simple-cluster-k8s) 
repo, specifically to support: 

 * Following recommendations from Lightbend regarding [good practices with Akka and Kubernetes](https://developer.lightbend.com/docs/akka-management/current/bootstrap/recipes.html#running-akka-cluster-in-kubernetes)
 * Running inside the Google Kubernetes Engine (GKE) with working default configs
 * Using the Kubernetes-API for bootstrap configuration
 * Running pods on separate nodes for proper redundancy (using anti-affinity)
 * Starting a minimal cluster in a non-K8s environment using the "config" bootstrap approach
 * Being run from a light weight Alpine base image (optional)
 * Running with IBM's OpenJ9 JVM (also optional)


### Running within GKE:

Configure GKE and get your local [kubectl configured](https://cloud.google.com/kubernetes-engine/docs/how-to/cluster-access-for-kubectl).

Ensure you have push access to GCR, and update your [local docker authorization](https://cloud.google.com/container-registry/docs/advanced-authentication).

Create a minimum of 3 nodes in your cluster (or adjust the replicas count in the deployment.yaml file).

I've only tested this on Kubernetes 1.12.5 - it may not work on earlier versions.

Let's go ahead and work in a separate namespace called *akka-cluster1*:

```bash
$ kubectl create namespace akka-cluster1
$ kubectl config set-context $(kubectl config current-context) --namespace=akka-cluster1
```

Now bring up the nodes:

```bash
$ cd k8s
$ ./setup.sh
```

This will create the Service Account and necessary Role and Role Binding, and create
the deployment.

If you wish to terminate the objects we created, you can run ./teardown.sh


### Running locally

If you'd like to run outside of a Kubernetes cluster but still bring up an Akka cluster on your local machine, you
can do that easily by passing configurations into the app that override the default config (via system properties
or environment variables). Since we're on a single machine, we have to choose different ports to avoid conflicts.

Eg:

```bash
$ sbt -DMGMT_HOSTNAME=localhost -DPORT=2551 -DAPI_PORT=8080 -DMGMT_PORT=8558 run
$ sbt -DMGMT_HOSTNAME=localhost -DPORT=2552 -DAPI_PORT=8081 -DMGMT_PORT=8559 run
```

Or do an analogous run command in your IDE of choice.

### Understanding the pieces

TODO


### Important notes

TODO: node anti-affinity

TODO: pod disruption budgets (for draining)

### Considerations

This is only a simple cluster - in a production environment you may wish to separate out seed nodes from worker
nodes by running two separate deployments.

Using a straight deployment is suitable for rolling upgrades if your systems can co-exist with two concurrent
containers running different versions. If you need two separate Actor System clusters, you may wish to look
into perhaps versioning the actor system name, and/or using a service mesh like Istio to segregate the 
communication between the deployed versions.

The $HOSTNAME environment variable is populated in GKE, but it uses the pod name. Using this as the hostname for the 
Akka Management can stump the election process - so we've provided an override as MGMT_HOSTNAME instead.

### Test talking to a REST service on a pod:


```bash
$ kubectl port-forward pod/akka-cluster-864d66cb75-4srw7 8080:8080

$ curl localhost:8080/ 
```

### Teardown:


To try it locally on minikube:
```bash
minikube start
eval $(minikube docker-env)
sbt docker:publishLocal
# create serviceAccount and role
kubectl create -f k8s/simple-akka-cluster-rbac.yml
# create deployment
kubectl create -f k8s/simple-akka-cluster-deployment.yml
# create service
kubectl create -f k8s/simple-akka-cluster-service.yml
KUBE_IP=$(minikube ip)
MANAGEMENT_PORT=$(kubectl get svc akka-simple-cluster -ojsonpath="{.spec.ports[?(@.name==\"management\")].nodePort}")
curl http://$KUBE_IP:$MANAGEMENT_PORT/cluster/members | jq
```

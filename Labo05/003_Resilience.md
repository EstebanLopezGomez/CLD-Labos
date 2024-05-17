# Task 3 - Add and exercise resilience

By now you should have understood the general principle of configuring, running and accessing applications in Kubernetes. However, the above application has no support for resilience. If a container (resp. Pod) dies, it stops working. Next, we add some resilience to the application.

## Subtask 3.1 - Add Deployments

In this task you will create Deployments that will spawn Replica Sets as health-management components.

Converting a Pod to be managed by a Deployment is quite simple.

  * Have a look at an example of a Deployment described here: <https://kubernetes.io/docs/concepts/workloads/controllers/deployment/>

  * Create Deployment versions of your application configurations (e.g. `redis-deploy.yaml` instead of `redis-pod.yaml`) and modify/extend them to contain the required Deployment parameters.

  * Again, be careful with the YAML indentation!

  * Make sure to have always 2 instances of the API and Frontend running. 

  * Use only 1 instance for the Redis-Server. Why?

    > On utilise une seule instance de redis éviter d'avoir des soucis de synchronisation entre les données en mémoires. 
	  On pourrait avoir des données incohérentes entre plusieurs instances puisqu'on ne vas pas configurer la réplication entre les instances.

  * Delete all application Pods (using `kubectl delete pod ...`) and replace them with deployment versions.

  * Verify that the application is still working and the Replica Sets are in place. (`kubectl get all`, `kubectl get pods`, `kubectl describe ...`)

## Subtask 3.2 - Verify the functionality of the Replica Sets

In this subtask you will intentionally kill (delete) Pods and verify that the application keeps working and the Replica Set is doing its task.

Hint: You can monitor the status of a resource by adding the `--watch` option to the `get` command. To watch a single resource:

```sh
$ kubectl get <resource-name> --watch
```

To watch all resources of a certain type, for example all Pods:

```sh
$ kubectl get pods --watch
```

You may also use `kubectl get all` repeatedly to see a list of all resources.  You should also verify if the application stays available by continuously reloading your browser window.

  * What happens if you delete a Frontend or API Pod? How long does it take for the system to react?
    > Le système voit qu'il n'y a pas le nombre de PODS et en crée une presque immédiatement.
    
  * What happens when you delete the Redis Pod?

    > L'api devient innacessible/inutilisable tant que le nouveau POD n'est pas recrée
    
  * How can you change the number of instances temporarily to 3? Hint: look for scaling in the deployment documentation

    > kubectl scale deployment redis-deploy --replicas=3
    
  * What autoscaling features are available? Which metrics are used?

    > Depuis notre page Google Cloud on peut avoir des grapgiques sur l'utilisation du processeur (CPU usage), de la mémoire (memory usage) et du disque (volume usage) du Pod désiré. 
    
  * How can you update a component? (see "Updating a Deployment" in the deployment documentation)

    > On doit modifier le fichier yaml puis lancer la commande : kubectl apply -f deployment.yaml . On peut aussi lancer la commande kubectl edit <fichier> pour le faire. Ensuite Kubernetes s'occupe de mettre à jour les Pods avec la nouvelle version (ce concept s'appelle "rollout").

## Subtask 3.3 - Put autoscaling in place and load-test it

On the GKE cluster deploy autoscaling on the Frontend with a target CPU utilization of 30% and number of replicas between 1 and 4. 

Load-test using Vegeta (500 requests should be enough).

> [!NOTE]
>
> - The autoscale may take a while to trigger.
>
> - If your autoscaling fails to get the cpu utilization metrics, run the following command
>
>   - ```sh
>     $ kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
>     ```
>
>   - Then add the *resources* part in the *container part* in your `frontend-deploy` :
>
>   - ```yaml
>     spec:
>       containers:
>         - ...:
>           env:
>             - ...:
>           resources:
>             requests:
>               cpu: 10m
>     ```
>

## Deliverables

Document your observations in the lab report. Document any difficulties you faced and how you overcame them. Copy the object descriptions into the lab report.

> // TODO


```````sh
C:\Windows\System32>kubectl describe deployment
Name:                   api
Namespace:              default
CreationTimestamp:      Thu, 16 May 2024 17:22:29 +0200
Labels:                 app=todo
                        component=api
Annotations:            deployment.kubernetes.io/revision: 1
Selector:               app=todo,component=api
Replicas:               2 desired | 2 updated | 2 total | 0 available | 2 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=todo
           component=api
  Containers:
   api:
    Image:      docker.io/icclabcna/ccp2-k8s-todo-api
    Port:       8081/TCP
    Host Port:  0/TCP
    Environment:
      REDIS_ENDPOINT:  redis-svc
      REDIS_PWD:       ccp2
    Mounts:            <none>
  Volumes:             <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Progressing    True    NewReplicaSetAvailable
  Available      False   MinimumReplicasUnavailable
OldReplicaSets:  <none>
NewReplicaSet:   api-59748d7b66 (2/2 replicas created)
Events:          <none>


Name:                   frontend
Namespace:              default
CreationTimestamp:      Thu, 16 May 2024 17:22:34 +0200
Labels:                 app=todo
                        component=frontend
Annotations:            deployment.kubernetes.io/revision: 2
Selector:               app=todo,component=frontend
Replicas:               2 desired | 2 updated | 2 total | 2 available | 0 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=todo
           component=frontend
  Containers:
   frontend:
    Image:      docker.io/icclabcna/ccp2-k8s-todo-frontend
    Port:       8080/TCP
    Host Port:  0/TCP
    Requests:
      cpu:  100m
    Environment:
      API_ENDPOINT_URL:  http://api-svc:8081
    Mounts:              <none>
  Volumes:               <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      True    MinimumReplicasAvailable
  Progressing    True    NewReplicaSetAvailable
OldReplicaSets:  frontend-7d9589956c (0/0 replicas created)
NewReplicaSet:   frontend-7fbdddfbb5 (2/2 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  44s   deployment-controller  Scaled up replica set frontend-7fbdddfbb5 to 1
  Normal  ScalingReplicaSet  41s   deployment-controller  Scaled down replica set frontend-7d9589956c to 1 from 2
  Normal  ScalingReplicaSet  41s   deployment-controller  Scaled up replica set frontend-7fbdddfbb5 to 2 from 1
  Normal  ScalingReplicaSet  39s   deployment-controller  Scaled down replica set frontend-7d9589956c to 0 from 1


Name:                   redis
Namespace:              default
CreationTimestamp:      Thu, 16 May 2024 17:21:52 +0200
Labels:                 app=todo
                        component=redis
Annotations:            deployment.kubernetes.io/revision: 1
Selector:               app=todo,component=redis
Replicas:               1 desired | 1 updated | 1 total | 1 available | 0 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=todo
           component=redis
  Containers:
   redis:
    Image:      redis
    Port:       6379/TCP
    Host Port:  0/TCP
    Args:
      redis-server
      --requirepass ccp2
      --appendonly yes
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      True    MinimumReplicasAvailable
  Progressing    True    NewReplicaSetAvailable
OldReplicaSets:  <none>
NewReplicaSet:   redis-56fb88dd96 (1/1 replicas created)
Events:          <none>
```````

```yaml
# redis-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis
  labels:
    app: todo
    component: redis
spec:
  replicas: 1
  selector:
    matchLabels:
      app: todo
      component: redis
  template:
    metadata:
      labels:
        app: todo
        component: redis
    spec:
      containers:
        - name: redis
          image: redis
          ports:
            - containerPort: 6379
          args:
            - redis-server 
            - --requirepass ccp2 
            - --appendonly yes
```

```yaml
# api-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api
  labels:
    app: todo
    component: api
spec:
  replicas: 2
  selector:
    matchLabels:
      app: todo
      component: api
  template:
    metadata:
      labels:
        app: todo
        component: api
    spec:
      containers:
        - name: api
          image: docker.io/icclabcna/ccp2-k8s-todo-api
          ports:
            - containerPort: 8081
          env:
            - name: REDIS_ENDPOINT
              value: redis-svc
            - name: REDIS_PWD
              value: ccp2
```

```yaml
# frontend-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend
  labels:
    app: todo
    component: frontend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: todo
      component: frontend
  template:
    metadata:
      labels:
        app: todo
        component: frontend
    spec:
      containers:
        - name: frontend
          image: docker.io/icclabcna/ccp2-k8s-todo-frontend
          ports:
            - name: http
              containerPort: 8080
          env:
            - name: API_ENDPOINT_URL
              value: http://api-svc:8081
          resources:
            requests:
              cpu: 100m
```

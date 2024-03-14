* What is the smallest and the biggest instance type (in terms of
  virtual CPUs and memory) that you can choose from when creating an
  instance?

```
The smallest instance that can be choosed is the t2.nano with : 1 vCPU and 0.
5 GiB of memory. The biggest instance that can be choosed is the x1e.
32xlarge/x2iedn.metal ith 128 vCPU and 4096 GiB of memory.
```

* How long did it take for the new instance to get into the _running_
  state?

```
It took less than a minute for the new instance to get into the _running_ state.
```

* Using the commands to explore the machine listed earlier, respond to
  the following questions and explain how you came to the answer:

    * What's the difference between time here in Switzerland and the time set on
      the machine?
      
    ```
    The machine has 1 hour less than the time here in Switzerland.
    ```

    * What's the name of the hypervisor?
    
    ```
    Xen
    ```

    * How much free space does the disk have?
    
    ```
    6.1 GB
    ```


* Try to ping the instance ssh srv from your local machine. What do you see?
  Explain. Change the configuration to make it work. Ping the
  instance, record 5 round-trip times.

```
TODO
```

* Determine the IP address seen by the operating system in the EC2
  instance by running the `ifconfig` command. What type of address
  is it? Compare it to the address displayed by the ping command
  earlier. How do you explain that you can successfully communicate
  with the machine?

```
10.0.6.13/28
```

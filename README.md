IPFS API wrapper library in Clojure 
===================================

> A **very** simple wrapper for interacting with [IPFS](https://ipfs.io) API

# Get it
```clojure
[clj-ipfs-api "1.0"]

;; In your ns statement:
(ns my.ns
  (:require [clj-ipfs-api.core :refer :all]))
```

# Usage
Launch the [IPFS Daemon](https://ipfs.io/docs/getting-started/).

```clojure
;; for default daemon settings use just like cli
;; commands are keywords
(ipfs :swarm :peers)
;; arguments are strings
(ipfs :swarm :disconnect "/ip4/54.93.113.247/tcp/48131/ipfs/QmUDS3nsBD1X4XK5Jo836fed7SErTyTuQzRqWaiQAyBYMP" "/ip4/188.166.8.195/tcp/4001/ipfs/QmNU1Vpryj5hfSmybSYHnS497ttgy9aNJ3T2B8wY2uMso4")
;; flags go in a map
(ipfs :object :get "QmaaqrHyAQm7gALkRW8DcfGX3u8q9rWKnxEMmf7m9z515w" {:encoding "json"}) 

;; for different API server use ipfs-custom, all options work the same
(ipfs-custom "http://127.0.0.1:55555" :swarm :peers)

;; exception is thrown, if wrong command or flag key is passed
```

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

Copyright © 2015- Piotr Czaban 

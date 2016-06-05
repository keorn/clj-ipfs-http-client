IPFS API wrapper in Clojure 
===================================

> A simple wrapper for interacting with [IPFS](https://ipfs.io) API

## Get it
```clojure
[clj-ipfs-api "1.2.1"]

;; In your ns statement:
(ns my.ns
  (:require [clj-ipfs-api.core :as ipfs]))
```

## Usage
Launch the [IPFS Daemon](https://ipfs.io/docs/getting-started/).

```clojure
;; for default daemon settings use just like cli
;; commands are joined with dashes
(ipfs/swarm-peers)
;; arguments are strings
(ipfs/cat
  "QmShWPeTZL5px2YGvgJD99C4SuHEqry1u1RoNu1bAVDkM1"
  "QmbRdyLXiFWrKc5hW1NbvpUxF9tLovWCPgiz4BDhjD9k3j")
;; flags go in a map
(ipfs/swarm-peers {:type "indirect"}) 
```

Custom API server address has to be set up.
```clojure
(ipfs/set-api-url! "http://127.0.0.1:55555")
```

Everything is decoded from json, except `cat`.
To change request options use a map under `:request` key.

```clojure
;; different API server
(ipfs/swarm-peers {:request {:url "http://127.0.0.1:55555"}})

;; for big files use a stream, no json parsing is done in this case
(ipfs/swarm-peers {:request {:as :stream}})
```

For more options that are taken by the `:request` map,
look at the second argument of request function in [clj-http](https://github.com/dakrone/clj-http#raw-request).

### License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

Copyright © 201 keorn

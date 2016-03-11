(ns clj-ipfs-api.core
  (require [clj-http.client :as client]
           [clojure.string :refer [join]]))

(defn- check-commands [commands]
  (if (and 
        (some? commands)
        (not-any?
          (partial contains? #{:ping :cat :block :get :dht :restart :stat :init :daemon :replace
                               :refs :bootstrap :name :connect :peers :edit :net :ls :pin :add
                               :level :commands :gc :findprovs :list :tail :id :swarm :resolve
                               :disconnect :repo :addrs :findpeer :put :mount :version :query
                               :log :object :publish :links :rm :show :data :diag :config :local})
          commands))
      (throw (Exception. "Wrong IPFS API argument."))))

(defn- check-flags [flag-keys]
  (if (and 
        (some? flag-keys)
        (not-any?
          (partial contains? #{:encoding :enc})
          flag-keys))
      (throw (Exception. "Wrong IPFS API flag key."))))

(defn- assemble-query-params [params ipfs-args]
  (let [{commands true, other false} (group-by keyword? ipfs-args)
        {args true, flags false}     (group-by string? other)
        suffix                       (join "/" (map name commands))
        full-url                     (str (:url params) "/api/v0/" suffix)]
    (check-commands commands)
    (check-flags (keys flags))
    (assoc params 
           :method :get
           :url full-url
           :query-params (if args (assoc flags :arg args)))))

(defn- api-request [query-params]
  (let [{:keys [status headers body error]} (client/request query-params)]
    (println headers)
    (if error
        (println "Failed with exception: " error)
        body)))

(defn ipfs-custom [request-params & ipfs-args]
  (let [query-params (assemble-query-params (merge {:url "http://127.0.0.1:5001" :as :json}
                                                   request-params)
                                            ipfs-args)]
    (api-request query-params)))

(def ipfs (partial ipfs-custom {})) 

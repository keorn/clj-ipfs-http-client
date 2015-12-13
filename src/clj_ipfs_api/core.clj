(ns clj-ipfs-api.core
  (require [cheshire.core :refer [decode]]
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

(defn- assemble-suffix [commands args [flags]]
  (check-commands commands)
  (check-flags (keys flags))
  (let [joined-commands (join "/" (map name commands))
        prefixed-args   (map (partial str "arg=") args)
        infixed-flags   (for [[flag value] flags] (str (name flag) "=" value))]
    (str joined-commands "?" (join "&" (concat prefixed-args infixed-flags)))))

(defn ipfs-custom [server & all]
  (let [{commands true, other false} (group-by keyword? all)
        {args true, flags false}     (group-by string? other)
        suffix                       (assemble-suffix commands args flags)
        address                      (str server "/api/v0/" suffix)] 
    (decode (slurp address))))

(def ipfs (partial ipfs-custom "http://127.0.0.1:5001")) 

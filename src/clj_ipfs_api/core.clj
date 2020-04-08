(ns clj-ipfs-api.core
  (:require [org.httpkit.client :as http]
            [cheshire.core :refer [parse-string]]
            [clojure.string :refer [join]]
            [clj-ipfs-api.commands :as c])
  (:refer-clojure :exclude [get resolve update cat]))

(def ^:private api-url (atom "http://127.0.0.1:5001"))

(defn- assemble-query
  "Assemble a map ready for request."
  [cmd-vec all-args]
  (let [{args true, [params] false} (group-by string? all-args)
        base-url                    (clojure.core/get (:request params) :url @api-url)
        full-url                    (str base-url "/api/v0/" (join "/" cmd-vec))
        ipfs-params                 (dissoc params :request)]
    ; text for cat, json for everything else
    (assoc (merge {:as (if (= (last cmd-vec) "cat") :text :json)
                   :method :post}
                  (:request params))
           :url full-url
           :query-params (if args (assoc ipfs-params :arg args) ipfs-params))))

(defn- api-request
  "The same as used by clj-http."
  [raw-map]
  (let [json?       (= :json (:as raw-map)) ; Fiddle around to make it look the same as clj-http
        request-map (conj raw-map (when json? [:as :text]))
        {:keys [status headers body error]} @(http/request request-map)]
    (when-not error (if json? (parse-string body true) body))))

; Bootstrapping using `ipfs commands`
(defn- empty-fn
  "Template function used for generation."
  [cmd-vec]
  (fn [& args]
    (api-request (assemble-query cmd-vec args))))

(defn- unpack-cmds
  "Traverse the nested structure to get vectors of commands."
  [acc cmds]
  (mapcat (fn [{:keys [:Name :Subcommands]}]
            (if (empty? Subcommands)
                (list (conj acc Name))
                (unpack-cmds (conj acc Name) Subcommands)))
          cmds))

(defn- intern-cmds [cmd-vecs]
  (doseq [cmd-vec cmd-vecs]
    (intern *ns*
            (symbol (join "-" cmd-vec))
            (empty-fn cmd-vec))))

(defn setup!
  "Request and intern all of the commands."
  []
  (if-let [cmd-raw  ((empty-fn ["commands"]))]
    (let [cmd-vecs (unpack-cmds [] (:Subcommands cmd-raw))]
      (intern-cmds cmd-vecs))
    ;; In the case that we can't use the server to get commands...
    (do (println "Could not set up using the" @api-url "address, please pick one with `set-api-url!`.")
        (intern-cmds c/cmd-vecs))))

(setup!) ; Try to setup using the default address.

(defn set-api-url! [new-url] (reset! api-url new-url) (setup!))

(ns cmr.umm.validation.parent-threading
  "Provides functions to thread together a granule and collection parent objects for validation.
  It weaves together the objects so matching items within the granule and collection are combined"
  (:require [cmr.umm.collection :as c]
            [cmr.umm.granule :as g]
            [cmr.common.util :as u])
  (:import [cmr.umm.granule
            CollectionRef
            DataGranule
            GranuleTemporal
            OrbitCalculatedSpatialDomain
            Orbit
            ProductSpecificAttributeRef
            SensorRef
            InstrumentRef
            PlatformRef
            SpatialCoverage
            TwoDCoordinateSystem
            UmmGranule]))

(defprotocol ParentThreader
  (set-parent [obj parent] "Sets the parent attribute on this object with the given parent"))

(comment

  ;; This code will be useful in the future. I'm waiting until we actually need it.
  (defn- set-parents-by-name
    ([objs parent-objs]
     (set-parents-by-name objs parent-objs :name))
    ([objs parent-objs name-field]
     ;; We'll assume there's only a single parent object with a given name
     (let [parent-obj-by-name (u/map-values first (group-by name-field parent-objs))]
       (for [child objs
             :let [parent (parent-obj-by-name (name-field child))]]
         (set-parent child parent)))))
  )

(extend-protocol
  ParentThreader

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  UmmGranule
  (set-parent
    [granule coll]

    (-> granule
        (assoc :parent coll)
        (update-in [:spatial-coverage] set-parent (:spatial-coverage coll))))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  clojure.lang.IRecord
  ;; Default implementation of set-parent for records
  (set-parent
    [obj parent]
    (assoc obj :parent parent))


  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; The protocol is extended to nil so we can attempt to set the parent on items which do not have
  ;; a parent
  nil
  (set-parent
    [_ _]
    nil)
  )
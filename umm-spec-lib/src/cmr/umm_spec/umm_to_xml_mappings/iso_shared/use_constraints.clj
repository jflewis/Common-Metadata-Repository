(ns cmr.umm-spec.umm-to-xml-mappings.iso-shared.use-constraints
  "Functions for generating ISO-19115 and ISO-SMAP XML elements from UMM UseConstraints and AccessConstraints records.")

(defn generate-user-constraints
  "Returns the constraints appropriate for the given metadata."
  [c]
  (let [description (get-in c [:AccessConstraints :Description])
        value (get-in c [:AccessConstraints :Value])
        use-constraints (:UseConstraints c)
        uc-description (:Description (:Description use-constraints))
        license-url (:LicenseUrl use-constraints)
        license-text (:LicenseText use-constraints)]
    [:gmd:resourceConstraints
     (when (or description value use-constraints)
       [:gmd:MD_LegalConstraints
        (when uc-description
          [:gmd:useLimitation
            [:gco:CharacterString uc-description]])
        (when description
          [:gmd:useLimitation
            [:gco:CharacterString (str "Restriction Comment: " description)]])
        (when (or license-url license-text)
          [:gmd:useConstraints
            [:gmd:MD_RestrictionCode
              {:codeList "https://cdn.earthdata.nasa.gov/iso/resources/Codelist/gmxCodelists.xml#MD_RestrictionCode"
               :codeListValue "otherRestrictions"} "otherRestrictions"]])
        (when license-url
          [:gmd:otherConstraints
            [:gco:CharacterString (str "LicenseUrl:" (:Linkage license-url))]])
        (when license-text
          [:gmd:otherConstraints
            [:gco:CharacterString (str "LicenseText:" license-text)]])
        (when value
          [:gmd:otherConstraints
            [:gco:CharacterString (str "Restriction Flag:" value)]])])]))
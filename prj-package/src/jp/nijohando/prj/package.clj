(ns jp.nijohando.prj.package
  (:require [clojure.java.io :as io]
            [cemerick.pomegranate.aether :as aether]
            [jp.nijohando.prj.package.pom :as pom]))

(defn- snapshot?
  [version]
  (.endsWith version "-SNAPSHOT"))

(defn update-pom
  [pom-file {:keys [group-id artifact-id version]}]
  (pom/update (io/file pom-file)
              {[pom/group-id] group-id
               [pom/artifact-id] artifact-id
               [pom/version] version}))

(defn deploy
  [{:keys [pom-file jar-file repository]}]
  (java.lang.System/setProperty "aether.checksums.forSignature" "true")
  (aether/register-wagon-factory! "http" #(org.apache.maven.wagon.providers.http.HttpWagon.))
  (let [{:keys [url username password]} repository
        {:keys [group-id artifact-id version]} (pom/select (io/file pom-file)
                                                           {:group-id [pom/group-id]
                                                            :artifact-id [pom/artifact-id]
                                                            :version [pom/version]})
        coordinates [(symbol group-id artifact-id) version]
        artifact-map (merge {}
                            (when-not (snapshot? version)
                              {[:extension "pom.asc"] (io/file (str pom-file ".asc"))
                               [:extension "jar.asc"] (io/file (str jar-file ".asc"))}))
        repo {:default {:url url
                        :username username
                        :password password}}]
    (aether/deploy :coordinates coordinates
                   :artifact-map artifact-map
                   :jar-file jar-file
                   :pom-file pom-file
                   :repository repo)))

(defn install
  [{:keys [pom-file jar-file local-repo-path]}]
  (let [{:keys [group-id artifact-id version]} (pom/select (io/file pom-file)
                                                           {:group-id [pom/group-id]
                                                            :artifact-id [pom/artifact-id]
                                                            :version [pom/version]})
        coordinates [(symbol group-id artifact-id) version]
        artifact-map (merge {}
                            (when-not (snapshot? version)
                              {[:extension "pom.asc"] (io/file (str pom-file ".asc"))
                               [:extension "jar.asc"] (io/file (str jar-file ".asc"))}))]
    (aether/install :coordinates coordinates
                    :artifact-map artifact-map
                    :jar-file jar-file
                    :pom-file pom-file
                    :local-repo local-repo-path)))

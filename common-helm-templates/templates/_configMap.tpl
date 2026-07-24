{{- define "common-helm-templates.config-map" -}}
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ include "common-helm-templates.configMapName" . }}
data:
   {{- range $key, $value := .Values.configMap.data}}
   {{ $key }}: {{ $value | quote }}
   {{- end}}
{{- end -}}
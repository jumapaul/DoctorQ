{{- define "common-helm-templates.configMapName" -}}
{{ .Chart.Name }}-config-map
{{- end -}}
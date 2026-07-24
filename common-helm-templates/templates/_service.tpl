{{- define "common-helm-templates.service" -}}
apiVersion: v1
kind: Service
metadata:
  name: {{ .Release.Name }}-service
spec:
  selector:
    app: {{ .Chart.Name }}
  type: {{ .Values.serviceType }}
  ports:
    - protocol: TCP
      port: {{ .Values.servicePort | default 80 }}  # Port ingress targets
      targetPort: {{ .Values.targetPort }}
{{- end -}}
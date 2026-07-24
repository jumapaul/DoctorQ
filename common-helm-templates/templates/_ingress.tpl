{{- define "common-helm-templates.ingress" -}}
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: {{ .Release.Name }}-ingress
  annotations:
    kubernetes.io/ingress.class: nginx
spec:
  rules:
    - host: {{ .Values.ingress.host | default "hello-world.local" }}
      http:
        paths:
          - path: {{ .Values.ingress.path | default "/" }}
            pathType: {{ .Values.ingress.pathType | default "ImplementationSpecific" }}
            backend:
              service:
                name: {{ .Release.Name }}-service
                port:
                  number: {{ .Values.servicePort | default 80 }}
{{- end -}}
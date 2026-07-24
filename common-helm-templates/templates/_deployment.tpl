{{- define "common-helm-templates.deployment" -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Release.Name }}-deployment
  labels:
    app: {{ .Chart.Name }}
spec:
  selector:
    matchLabels:
      app: {{ .Chart.Name }}
  template:
    metadata:
      labels:
        app: {{ .Chart.Name }}
    spec:
      containers:
        - name: {{ .Release.Name }}
          image: {{ .Values.image.repository }}:{{ .Values.image.tag }}
          ports:
            - containerPort: {{ .Values.containerPort }}
          livenessProbe:
            httpGet:
              path: {{ .Values.probes.liveness.path }}
              port: {{ .Values.probes.liveness.port }}
            initialDelaySeconds: {{ .Values.probes.liveness.initialDelaySeconds }}
            periodSeconds: {{ .Values.probes.liveness.periodSeconds }}
            failureThreshold: {{ .Values.probes.liveness.failureThreshold }}
          readinessProbe:
            httpGet:
              path: {{ .Values.probes.readiness.path }}
              port: {{ .Values.probes.readiness.port }}
            initialDelaySeconds: {{ .Values.probes.readiness.initialDelaySeconds }}
            periodSeconds: {{ .Values.probes.readiness.periodSeconds }}
            failureThreshold: {{ .Values.probes.readiness.failureThreshold }}
          {{- if .Values.env }}
          env:
            {{- if .Values.configMap }}
            {{- range $key, $value := .Values.configMap.data }}
            - name: {{ $key }}
              valueFrom:
                configMapKeyRef:
                  name: {{ include "common-helm-templates.configMapName" $ }}
                  key: {{ $key }}
          {{- end }}
          {{- end }}
          {{- range .Values.env }}
            - name: {{ .name }}
              valueFrom:
                {{- toYaml .valueFrom | nindent 16 }}
            {{- end }}
          {{- end }}
          {{- if .Values.secretVolume }}
          volumeMounts:
            - name:  {{ .Values.secretVolume.name }}
              mountPath: {{ .Values.secretVolume.mountPath }}
          {{- end }}
      {{- if .Values.secretVolume }}
      volumes:
        - name: {{ .Values.secretVolume }}
          secret:
            secretName: {{ .Values.secretVolume.secretName }}
      {{- end }}
{{- end -}}
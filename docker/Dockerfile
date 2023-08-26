FROM prom/prometheus:latest

WORKDIR /app

COPY target/my-app.jar /app
COPY grafana.ini /etc/grafana/grafana.ini
COPY datasource.yml /etc/grafana/provisioning/datasources/datasource.yml

EXPOSE 8080 3000

ENTRYPOINT [ "prometheus" ]
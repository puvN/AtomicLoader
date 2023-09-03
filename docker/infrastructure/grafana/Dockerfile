FROM grafana/grafana:latest

# Copy your Grafana configuration file to the container
COPY custom.ini /etc/grafana/custom.ini

# Copy any provisioning files (datasources, dashboards, etc.) if needed
COPY provisioning /etc/grafana/provisioning/

# Expose the default Grafana port
EXPOSE 3000

# Start Grafana
CMD ["/run.sh"]
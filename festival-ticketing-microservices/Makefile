    .PHONY: build test up down logs validate-k8s

    build:
	    mvn -B -ntp clean package -DskipTests

    test:
	    mvn -B -ntp verify

    up:
	    docker compose up --build -d

    down:
	    docker compose down

    logs:
	    docker compose logs -f --tail=200

    validate-k8s:
	    kubectl apply --dry-run=client --validate=false -k k8s/

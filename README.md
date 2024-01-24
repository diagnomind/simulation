# web-server
[![Java CI with Maven](https://github.com/diagnomind/simulation/actions/workflows/maven.yml/badge.svg)](https://github.com/diagnomind/simulation/actions/workflows/maven.yml)

[![Quality Gate Status](https://sonarqube.diagnomind.duckdns.org/api/project_badges/measure?project=simulation&metric=alert_status&token=sqb_a0910ec6530585686b9aead82afdac7cc7231262)](https://sonarqube.diagnomind.duckdns.org/dashboard?id=simulation)

## Description

Program that simulates the behaviour of a hospital, with and without using the previously developed model, with the purpose of comparing the time difference.

## Installation

To build this project Java 21 is needed, with the following command:
```
$> mvn clean package
```

To test the project execute:
```
$> mvn clean verify
```

To generate Javadoc documentation:
```
$> mvn javadoc:javadoc
```

## Credits

- Qing Yu Jiang Pan
- Diogo Sousa Fernandes
- Gaizka Sáenz de Samaniego Gonzalez
- Asier López Lorenzo
- Eñaut Genua Prieto

## License

This project is licensed under the [AGPLv3+](LICENSE).

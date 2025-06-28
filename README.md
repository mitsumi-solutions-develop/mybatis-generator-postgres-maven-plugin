# mybatis-generator-postgres-maven-plugin

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) [![Licence](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](./LICENSE)

# supported

- java version: 21

# support

## generate mybatis code flowing postgres extension types

- Enum
- Json
- UUID

## generatedKey column

if defined `{primary_key}`, find table's primary column to set generatedKey column.

```xml
<table tableName="tbl_%"
       enableInsert="true"
       enableSelectByPrimaryKey="true"
       enableUpdateByPrimaryKey="true"
       enableDeleteByPrimaryKey="true">
    <generatedKey column="{primary_key}" sqlStatement="JDBC" />
</table>
```

# dependency

## maven

```xml
<plugin>
    <groupId>io.github.mitsumi-solutions-develop</groupId>
    <artifactId>mybatis-generator-postgres-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <configurationFile>${project.basedir}/src/main/resources/generatorConfig.xml</configurationFile>
        <overwrite>true</overwrite>
        <includeAllDependencies>true</includeAllDependencies>
    </configuration>
</plugin>
```

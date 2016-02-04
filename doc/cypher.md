# Neo4j and Cypher

Neo4j uses the cypher query language. To see all links, which AfA has identified, use e.g.:

```cypher
MATCH ()-[r:`cites`]->() RETURN r
```

```bash
bin/neo4j-shell -path data/graph.db/
NOTE: Local Neo4j graph database service at 'data/graph.db/'
Welcome to the Neo4j Shell! Enter 'help' for a list of commands


neo4j-sh (?)$ cypher
> MATCH ()-[r:`cites`]->() RETURN r
> ;
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| r                                                                                                                                                                                          |
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| :cites[133210]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[1]/note[1]/location[1]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[1])"}  |
| :cites[133209]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[1]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[1])"}                      |
| :cites[133212]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[3]/note[1]/location[1]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[14])"} |
| :cites[133211]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[3]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[14])"}                     |
| :cites[133213]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[4]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[16])"}                     |
| :cites[133214]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[5]/p[1]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[27]/body[1]/div[4]/sp[7]/ab[1]/text()[1])"}                |
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
6 rows
2225 ms

```

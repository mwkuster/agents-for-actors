cy# Neo4j and Cypher

Neo4j uses the cypher query language. To see all links, which AfA has identified, use e.g.:

```cypher
MATCH (f)-[r:`cites`]->(t) RETURN f, r,t ;
```

```bash
bin/neo4j-shell -path data/graph.db/
NOTE: Local Neo4j graph database service at 'data/graph.db/'
Welcome to the Neo4j Shell! Enter 'help' for a list of commands


neo4j-sh (?)$ cypher
> MATCH ()-[r:`cites`]->() RETURN r
> ;
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| f                                                                                                                                                      | r                                                                                                                                                                                          | t                                                                                                                                                        |
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Node[133184]{name:"Now is the summer of our sweet content,",xptr:"xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[1]/text()[1])"}                             | :cites[133209]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[1]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[1])"}                      | Node[76941]{xptr:"xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[1])",name:"Now is the Winter of our Discontent,"}               |
| Node[133185]{xptr:"xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[1]/note[1]/location[1]/text()[1])",name:"Now is the Winter of our Discontent,"}            | :cites[133210]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[1]/note[1]/location[1]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[1])"}  | Node[76941]{xptr:"xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[1])",name:"Now is the Winter of our Discontent,"}               |
| Node[133188]{name:"And I that am not shaped for black-faced war,",xptr:"xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[3]/text()[1])"}                       | :cites[133211]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[3]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[14])"}                     | Node[76954]{name:"But I, that am not shap'd for sportiue trickes,",xptr:"xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[14])"}   |
| Node[133189]{name:"But I, that am not shap'd for sportiue trickes,",xptr:"xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[3]/note[1]/location[1]/text()[1])"} | :cites[133212]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[3]/note[1]/location[1]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[14])"} | Node[76954]{name:"But I, that am not shap'd for sportiue trickes,",xptr:"xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[14])"}   |
| Node[133190]{name:"I that am rudely cast and want true majesty,",xptr:"xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[4]/text()[1])"}                        | :cites[133213]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[2]/l[4]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[16])"}                     | Node[76956]{name:"I, that am Rudely stampt, and want loues Maiesty,",xptr:"xpointer(/TEI[1]/text[1]/group[1]/text[23]/body[1]/div[1]/ab[1]/text()[16])"} |
| Node[133196]{name:"I know not, My Lord. I'll ask my son.",xptr:"xpointer(/TEI[1]/text[1]/body[1]/sp[5]/p[1]/text()[1])"}                               | :cites[133214]{link-text:":cites:xpointer(/TEI[1]/text[1]/body[1]/sp[5]/p[1]/text()[1])->xpointer(/TEI[1]/text[1]/group[1]/text[27]/body[1]/div[4]/sp[7]/ab[1]/text()[1])"}                | Node[96472]{name:"My Lord I know not I, nor can I gesse,",xptr:"xpointer(/TEI[1]/text[1]/group[1]/text[27]/body[1]/div[4]/sp[7]/ab[1]/text()[1])"}       |
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
6 rows
177 ms

```

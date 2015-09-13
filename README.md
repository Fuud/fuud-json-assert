# fuud-json-assert 
# Tool for asserting that JSON confirms specification.

For example in your test you expect following JSON payload:
```
{
  "author": "Fuud",
  "commit-date": "13 sep 2015",
  "info": {
    "id": 123,
    "tags": ["json","java","tool"],
    "dependencies": [
      {
        "groupId": "org.spockframework",
        "artifactId": "spock-core-fuud-fork",
        "version": "1.0.4-groovy-2.3",
        "scope": "test"
      }
    ]
  }
}
```

And actual payload is
```
{
  "author": "Fuud",
  "commit-date": "14 sep 2015",
  "info": {
    "id": 125,
    "tags": [
      "json",
      "java",
      "tools"
    ],
    "dependencies": [
      {
        "groupId": "org.spockframework",
        "artifactId": "spock-core-fuad-fork",
        "version": "1.0.4-groovy-2.3",
        "scope": "test",
        "optional": false
      }
    ]
  }
}
```

By invoking `JsonAsserts.assertEquals(expected, actual)` you will get `AssertionError` with following message (but without comments):

```javascript
{                                                         {
  "author" : "Fuud",                                        "author" : "Fuud",
  "commit-date" : "13 sep 2015",                    <>      "commit-date" : "14 sep 2015", //<> means that fields is different
  "info" : {                                                "info" : {
    "tags" : [                                                "tags" : [ // all nested structures became multiline
      "json",                                                   "json",
      "java",                                                   "java",
      "tool"                                        <>          "tools" // thus you can easily find differences
    ],                                                        ],
    "id" : 123,                                     <>        "id" : 125,
    "dependencies" : [                                        "dependencies" : [
      {                                                         {
        "groupId" : "org.spockframework",                         "groupId" : "org.spockframework",
        "scope" : "test",                                         "scope" : "test",
        "artifactId" : "spock-core-fuud-fork",      <>            "artifactId" : "spock-core-fuad-fork",
        "version" : "1.0.4-groovy-2.3"                            "version" : "1.0.4-groovy-2.3",
                                                                  "optional": false // additional fields in objects are accepted
      }                                                         }
    ]                                                         ]
  }                                                         }
}                                                         }
```

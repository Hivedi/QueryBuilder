# QueryBuilder
Simple query builder

Repo:
```
repositories {
	maven {
		url "https://jitpack.io"
	}
}
```

Dependences:
```
dependencies {
	compile 'com.github.Hivedi:QueryBuilder:1.0.0'
}
```


### Sample code:
```java
QueryBuilder qb = new QueryBuilder();
qb.from("table")
	.where("field=?")
	.addParam(1);
	
Log.i("tests", "SQL: " + qb.getSelect());
```
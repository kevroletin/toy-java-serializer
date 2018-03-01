# Json serializer/desealizer

See 
[Main.java](src/main/java/io/github/kevroletin/Main.java) for sample usage. 

See
[Json.java](src/main/java/io/github/kevroletin/Json.java) for a list of API functions. 

There are also two annotations in
[annotations](src/main/java/io/github/kevroletin/json/annotations) directory.

## Restrictions and behavior details 

+ Ther are restrictions for deserializable object:

  + Class should implement public default constructor *(constructor with no
    arguments)*, otherwise serializer will not be able to create object of that
    class.

  + Fields should be public.

  + No support for generics. The reason for that is that due to type erasure there
    is not sufficient information in runtime to find actual parameter types
    *(probably I missed some hidden magic though)*.

+ Trailing input is just ignored.

+ Missed field in Json object is treated as error during serialization.

## TODOs

+ Custom serializers/deserializers via annotations.

+ Skip fields via annotations.

+ Allow missed fields in Json input via fields annotations.

+ Uninformative error messages. 

+ No negative test for parser.

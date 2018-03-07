# Json serializer/desealizer

See 
[Main.java](src/main/java/io/github/kevroletin/Main.java) for sample usage. 

See
[Json.java](src/main/java/io/github/kevroletin/Json.java) for a list of API functions. 

There are also `Sanitizer` annotation.

## Initial requirements

+ No 3rd party libraries, pure Java.
+ Field sanitization via annotations.
+ Capable to process types defined in 3rd party libraries.
+ Returns list of all errors.

## Current progress

Serializer doesn't support extensions via type adapters yet.

## Sanitization

It is feature to restrict set of allowed values. Sanitization similar to
validation but in some cases sanitizer can turn invalid value into some allowed
"canonical" representation. A good example is a phone number. In some contexts
8(4232)1-23-45 and 12345 can refer to a same phone number and clearly it's good
idea to canonicalize then before usage.

The are two approaches for sanitization. First one is via fields annotations
like this:

    class User {

        @Sanitizer(cls = TelephoneNumber.TelephoneAsStringSanitizerFactory.class)
        public String strNumber;
        ...

And second one is via defining separate type and then registering corresponding
`TypeAdapter` like this.

    class User {

        public TelephoneNumber typesafeNumber;
    ...
    
      
    Json json = new JsonBuilder()
                .typeAdapter(TelephoneNumber.class, new TelephoneNumber.TelephoneNumberAdapter())
                .build();

A call such approach with a separate type a "type safe". One should add
validation into constructor of TelephoneNumber. So any time you meet
TelephoneNumber in your program - you can be sure it contains valid
representation of it's value.

That second approach also allows using TelephoneNumber as a key in dictionary like this:

        json.fromJson(
            "{\"71234567890\": true, \"70000000000\": false}",
            type
        ));

`type` can be defined like this: `new TypeToken<Map<TelephoneNumber,
Boolean>>(){}.getType();`.

## Restrictions and behavior details 

+ Ther are restrictions for deserializable object:

  + Class should implement public default constructor *(constructor with no
    arguments)*, otherwise serializer will not be able to create object of that
    class.

  + Fields should be public.

+ Missed field in Json object is treated as error during serialization.

+ Nulls are placed into resulting json during serialization.

## TODOs

+ TypeAdapters for serializer.
+ Skip fields via annotations.
+ Allow missed fields in Json input via fields annotations.

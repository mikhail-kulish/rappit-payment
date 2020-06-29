# rappit-payment

This is a part of real project in production which I would like to share to have some discussion/comments on a new approach in micro-services design.
I've tried to step back from common pattern Controller-Service-Repository and replaced it with OOP and functional parts.
This way entities in project are actually more looks like real world representations in code, the code itself became more readable and maintainable.

Some principles that I was trying to follow:
1. Classes must implement interface.
2. No getters/setters (use Media instead of getters).
3. Objects are immutable as much as possible.
4. Objects represents real world entities (as much as possible).
5. Keep framework (Spring) outside of business code. Let it do only dependency, startup and network request handling.

Main outcome of such approach is:
1. No DTO, Converters, Mappers used.
2. No Service or Repository layer.
3. No data copying over entities.
4. Maximum length of java code file is 115 lines.
5. Average java file size is 43 lines of code.
6. 45 `null` in code (yes, I will count them and try to get to real minimum - 0 at least in business logic).
7. 21 `if` statements (I found it pretty nice indicator of clean code - less `if`s, cleaner the code).



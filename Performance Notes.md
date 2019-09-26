# Performance Notes

I wrote three classes to measure the performance of DogTags, and compare their performance with Apache's EqualsBulder. This gave me an opportunity to try a few different approaches to get the best possible performance out of my code. Here's what I learned.

## 1 Wrapper fields outperform primitive fields.
When I made all my fields were wrapper classes like Integer, DogTags comparisons ran more than twice as fast as when they used primitives like int. While I didn't investigate the reason, I assume the reason is that the code doesn't spend any time boxing and unboxing primitive values. My getter methods, for example, implement an interface method that looks like this: 

    R get(T object)
The type R can be an Integer, but it can't be an int. So if this method needs to return an int value, it will wrap it inside an Integer before returning, then unwrap it again afterwards. But if the class is an Integer, it doesn't need to do this. Of course, there are good reasons why returning wrapper classes are generally considered a poor practice over primitives. But Java's generic type mechanism doesn't handle primitive types very well.

## 2 For speed, DogTags beat EqualsBuilder, but hand-coding beats DogTags.
When I compare the speed difference of DogTags and EqualsBuilder, DogTags are anywhere from 1.5 times as fast to 20 times as fast, depending on how many fields are tested before a mismatch is found. If the mismatch is found early, DogTags can be about 20 times faster. But a hand-coded equals method is generally in the range of 25 times faster than a DogTag.

## 3 Functional Programming degrades performance.
This may come as a surprise, but the reasons have little to do with the Stream classes or their methods. In order to convert DogTags to use functional programming, I needed to get rid of the ThrowingFunction interface, which declares its one method to throw a checked exception. So I first had to rewrite the implementing code to wrap the thrown exception and rethrow it. This code was never executed, because the Exception can't be thrown under the circumstances. But it's necessary because, to use a Stream, I need a method that qualifies as a Predicate, which does not throw a checked exception. 

Once I made this change, but before I switched to functional code, I repeated my performance test, and discovered the code ran twice as slowly. Converting to functional programming didn't offer any speed improvements, and turning on parallel processing actually slowed down the code by a factor of 10 or so.

Here's the final loop of the doTestForEqual(T, T) written an functional programming style:

    return fieldProcessors.stream()
        .allMatch(
            f -> f.testForEquals(thisOneNeverNull, thatOneNeverNull)
        ); 

## Results

Here are the result s of a performance test comparing DogTags with EqualsBuilder:

[Performance Results](https://raw.githubusercontent.com/SwingGuy1024/DogTags/blob/master/Performance.pdf)

[png](https://raw.githubusercontent.com/SwingGuy1024/DogTags/blob/master/Performance.png)
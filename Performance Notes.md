# Performance Notes

I wrote three classes to measure the performance of DogTags, and compare their performance with Apache's `EqualsBulder.referenceEqual()`. This gave me an opportunity to try a few different approaches to get the best possible performance out of my code. Here's what I learned.

## 1 Wrapper fields outperform primitive fields.
When I used wrapper classes like Integer for all my fields, DogTags comparisons ran more than twice as fast as when I used primitives like int. While I didn't figure out the reason, I ruled out one candidate. My early code used `Field.get()` for primitives, so I though it might be spending too much time boxing and unboxing primitive values. But I rewrote it to use Fields primitive getters like `Field.getInt()`, which returns an int, not an Integer. This had no effect on performance. Wrapper classes still outperformed primitives. Of course, there are good reasons why returning wrapper classes are generally considered a poor practice over primitives.

## 2 For speed, DogTags whips EqualsBuilder.referenceEquals(), but hand-coding whips DogTags
When I compare the speed difference of DogTags and `EqualsBuilder.referenceEquals()`, DogTags are anywhere from 5 to 50 times as fast, depending on how many fields are tested before a mismatch is found. If the mismatch is found early, DogTags can be about 50 times faster. But a hand-coded equals method is generally in the range of 25 times faster than a DogTag. Also, using EqualsBuilder to build an equals method, while is the least convenient, it's also nearly as fast as hand-coding. Consequently, I need to be clear that DogTags does not replace EqualsBuilder entirely, but it's a good, much faster replacement of EqualsBuilder's `reflectionEquals()` method.

## 3 Functional Programming degrades performance.
This may come as a surprise, but the reasons have little to do with the Stream classes or their methods. In order to convert DogTags to use functional programming, I needed to get rid of the two "Throwing" functional interfaces, which declare a method to throw a checked exception. So I first had to rewrite the implementing code to wrap the thrown exception and rethrow it. This code was never executed, because the Exception can't be thrown under the circumstances. But it's necessary because, to use a Stream, I need a method that qualifies as a Predicate, which does not throw a checked exception.

Once I made this change, but before I switched to functional code, I repeated my performance test, and discovered the code ran twice as slowly. Converting to functional programming didn't offer any speed improvements, and turning on parallel processing actually slowed down the code by a factor of 10 or so.

Here's the final loop of the doTestForEqual(T, T) written a functional programming style:

    return fieldProcessors.stream()
        .allMatch(
            f -> f.testForEquals(thisOneNeverNull, thatOneNeverNull)
        ); 

## Results

Here are the result s of a performance test comparing DogTags with `EqualsBuilder.reflectionEquals()`:

![png](https://github.com/SwingGuy1024/DogTags/blob/master/Performance.png)


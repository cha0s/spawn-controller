# Spawn Controller

This mod is controlling spawns on my server. It's not very polished yet. It doesn't even generate its own config file. If you want to use it, make a configuration file likes this:

```
# Configuration file

# This is, roughly, "how many X can spawn around a player?"
mobcap {
  I:Hostile=70
  I:Animal=10
  I:Ambient=15
  I:Water=10
}

throttle {
  I:Hostile=0
  I:Animal=0
  I:Ambient=0
  I:Water=0
}
```

or it will probably just straight up crash.

# TODO

- Generate config
- Make /scstats reliable (sometimes it just doesn't work)
- I dunno, probably tons of stuff since this is my first mod lol

---

MIT license


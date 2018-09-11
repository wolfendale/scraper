Running from SBT:

```
sbt "run <args>"
```

```
Usage: scraper [options] <url to scrape> <output file>

  <url to scrape>
  <output file>
  -p, --printer <value>  the printer which should be used to render to the output file (default: dot)
  -t, --timeout <value>  the maximum time which the scraper should run for (default: 5s)

  printer options are:
    `dot`    for dot graph language output
    `simple` for a line-by-line output of scraped urls
```

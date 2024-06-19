### Creating a blog with Clojure and Markdown files

#### Steps
1. Create a markdown file for each blog post
2. Implement the functions to read and parse those files into a blog post
3. Convert the file from markdown to html.


#### Creating the markdown
This is the most straightforward part of the process.  Create a directory that will be the location for your blog posts.  In my case I put them in resources/public/posts/{year}.

The filename should also conform to the format {title}#mm-dd-yyyy.  That will act as metadata for the implementation of the parsing logic that will create the blog post.  

#### Implementing the blogging logic.
The goal is to be able to retrieve all blog files, parse their metadata, and read their contents.  First create the ns that will contain the blogging functions.

``blog.clj``

Define the location of your blog files

``(def posts-directory "resources/public/posts")``

Create a file so that you can iterate through them.

``(def posts-file (io/file posts-directory))``

Make a predicate to determine if this is a valid blog file.

```
(defn- blog-file? [file-name]
  (str/ends-with? file-name ".md"))
```

Create a function to retrieve the post files.  File-seq will return a sequence of all files in the directory, including the directory itself. Use the ``blog-file?`` predicate to filter out anything that is not a markdown file.
```
(defn- get-post-files []
  (filter blog-file? (file-seq posts-file)))
```

Now it's time for the parsing logic.  Create a function to help get the name of the file with Java interop.

```
(defn- get-file-name [^File file]
  (.getName file))
```

Be sure to add the type hint for ^File so it doesn't rely on reflection.  Adding reflection warnings is a good way to get reminders for this.

``(set! *warn-on-reflection* true)``

Now a function that will parse the name of the file to extract some useful metadata.  This will return a 2-element vector with the title and date of the post. ``["Creating a Blog" "06-19-2024"]``.
```
(defn- parse-file-name [filename]
  (-> filename
      (str/replace #".md" "")
      (str/split #"#")))
```

Using the functions created above, create a new function that creates a post from a file.  The returned value will be a vector with 3 elements.  The title, date, and contents of the file.
```
(defn ->post
    "Returns a tuple consisting of the title, date, and contents of a file, where file is named
    with the convention of post-title#mm-dd-yyyy.md"
    [file]
    (let [file-name (get-file-name file)
          contents (slurp file)
          [title date] (parse-file-name file-name)]
      [title date contents]))
```

Finally, putting it all together make a function to create a post for each of the blog files.
```
(defn get-all-posts []
  (map ->post (get-post-files)))
```

One more function will be useful to retrieve the specific blog post for a title.  This will check the first element of each post, which is the title, and compare it against the given title to see if they match.  Since filter returns a ``seq``, grab the first element.  If there is no match it will return ``nil``.

```
(defn get-post [^String title]
  (->> (get-all-posts)
       (filter (comp #(.equalsIgnoreCase title %) first))
       first))
```

#### Convert the markdown to html and make a route for each post.
Before converting the contents to html, the main page needs to display all of the recent blog posts.  This is a simple function that uses hiccup to display the html.

```
(defn blog-page []
  (page "Blog"
        [:h3 "Recent posts"]
        (let [all-posts (blog/get-all-posts)]
          [:ul
          (map (fn[[title date]]
                   [:span [:a {:href (str "/blog/" title " ")}  title] [:em " " date]]) all-posts)])))
                   
```
Each post will link to a standalone page that will display the contents of the blog.  The simplest way to turn the markdown into html is to use a library.  I found several good libraries for this, but settled on [commonmark-hiccup](https://github.com/bitterblue/commonmark-hiccup) for now.  Here's the simple function that gets the blog post from the title and converts it to html.

```
 (defn blog [title]
  (page title
        (md/markdown->hiccup (last (blog/get-post title)))))
```

I'm using [ring](https://github.com/ring-clojure/ring) and [compojure](https://github.com/weavejester/compojure) for my server and routing libraries.  Adding this to the routes is good enough to create a path for a post, using the title as the path parameter.

```(GET "/blog/:title" request (h/blog (str (:title (:params request)))))```

That's all that's needed for creating a simple blog with clojure and a few libraries.  I was motivated to create my blog by this [post](https://blog.michielborkent.nl/migrating-octopress-to-babashka.html). 

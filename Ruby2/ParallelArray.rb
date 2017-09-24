class Array
  private
  def parallel(method, *args)
    threads = []
    self.each do |elem|
      threads << Thread.new do
        Thread.current[:output] = yield elem
        Thread.current[:elem] = elem
      end
    end
    threads.send(method, *args) do |thread|
         thread.join
         thread[:output]
    end
  end
  
  private
  def smart_parallel(method, *args)
    res = parallel(method, *args) do |elem|
       yield elem
    end
    if (method == "select")
      res = res.map do |thread|
        thread[:elem]
      end
    end
    res
  end
  
  public
  def map_parallel
    parallel("map") do |elem|
      yield elem
    end
  end
  
  public
  def any_parallel?
    parallel("any?") do |elem|
      yield elem
    end
  end
  
  public
  def all_parallel?
    parallel("all?") do |elem|
      yield elem
    end
  end
  public
  def select_parallel
    smart_parallel("select") do |elem|
      yield elem
    end
  end
end


a = (3..20).map { |obj| obj }

puts a.select_parallel { |obj| 6 <= obj }

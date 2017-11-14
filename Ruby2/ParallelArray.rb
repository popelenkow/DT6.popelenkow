class MyPair
  attr_accessor :elem
  attr_accessor :output
end

class Array
    
  private
  def parallel(countThread, method, *args)
    output = Array.new(self.length)
    iter = 0
    semaphore = Mutex.new
    threads = Array.new
    id = 0
    
    (1..countThread).each do |_|
      threads << Thread.new do
        while true do
          semaphore.synchronize do
            Thread.current[:iter] = iter
            iter = iter + 1
          end  
          
          
          break if Thread.current[:iter] >= self.length
          #puts Thread.current.object_id
          
          Thread.current[:pair] = MyPair.new
          Thread.current[:pair].elem = self[Thread.current[:iter]]
          Thread.current[:pair].output = yield Thread.current[:pair].elem
     
          output[Thread.current[:iter]] = Thread.current[:pair]
        end
      end
    end
    
    threads.each do |thread|
      thread.join
    end
    output.send(method, *args) do |pair|
      pair.output
    end
  end
  
  private
  def smart_parallel(countThread, method, *args)
    res = parallel(countThread, method, *args) do |elem|
       yield elem
    end
    if (method == "select")
      res = res.map do |pair|
        pair.elem
      end
    end
    
    res
  end
  
  public
  def map_parallel(countThread = 2)
    parallel(countThread, "map") do |elem|
      yield elem
    end
  end
  
  public
  def any_parallel?(countThread = 2)
    parallel(countThread, "any?") do |elem|
      yield elem
    end
  end
  
  public
  def all_parallel?(countThread = 2)
    parallel(countThread, "all?") do |elem|
      yield elem
    end
  end
  public
  def select_parallel(countThread = 2)
    smart_parallel(countThread, "select") do |elem|
      yield elem
    end
  end
end


a = (3..20).map { |obj| obj }

puts a.select_parallel(8) { |obj| 6 <= obj }
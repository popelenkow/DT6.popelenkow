class Array
  private
  def parallel
    threads = []
    threads << Thread.new do
      res = yield(Thread.current)
    end
  end
  
  public
  def map_parallel
    map do |elem|
      thread =  parallel do
        elem = yield(elem)
      end
      thread.join
      elem
    end
  end
  
  public
  def any_parallel?
      block = false
      any? do |elem|
        parallel do |thread|
          thread[:local_block] = yield(elem)
          puts elem.to_s + " " + thread[:local_block].to_s + "/n"
          block |= thread[:local_block]
        end
        block
      end
  end
  
  
end


a = (1..20).map { |obj| obj }

puts a.map_parallel { |obj| 3+obj }

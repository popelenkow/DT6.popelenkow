class SystemData
  attr_accessor :level
  attr_accessor :time
  attr_accessor :source
  attr_accessor :event_code
  attr_accessor :task_category
  attr_accessor :general
  
  def to_s
      d = self
      res = d.level.to_s + "  " +
            d.time.day.to_s + "." + d.time.month.to_s + "." + d.time.year.to_s + " " +
            d.time.hour.to_s + ":" +  d.time.min.to_s + ":" + d.time.sec.to_s + "  " +
            d.source.to_s + "  " +
            d.event_code.to_s + "  " +
            d.task_category.to_s + " " +
            d.general.to_s
    end
end
  
class ParserSystemData
  @@rx =
  /
    ^
    ([а-яА-Я]+)           #level
    \s+
    (\d{1,2})             #time.day
    \.
    (\d{1,2})             #time.month
    \.
    (\d{2,4})             #time.year
    \s+
    (\d{1,2})             #time.hour
    \:
    (\d{1,2})             #time.min
    \:
    (\d{1,2})             #time.sec
    \s+
    ([а-яА-Я\w\-]+)       #source
    \s+
    (\d+)                 #event_code
    \s+
    ([а-яА-Я\w\-\(\)]+) #task_category
    \s+
    (.+)                  #general
    $
  /x
  def self.parse(systemDataString)
    @@rx.match(systemDataString)
  end
  
  def self.createData(systemDataString)
    res = parse(systemDataString)
    data = nil
    if (res != nil)
      data = SystemData.new()
      data.level = res.captures[0]
      i = 1
      data.time = Time.new(res.captures[2+i], res.captures[1+i], res.captures[i], res.captures[3+i], res.captures[4+i], res.captures[5+i])
      data.source = res.captures[7]
      data.event_code = res.captures[8]
      data.task_category = res.captures[9]
      data.general = res.captures[10]
    end
    data
  end
end

class Command
  attr_accessor :name_source
  attr_accessor :name_destination
  attr_accessor :command
  attr_accessor :level
  attr_accessor :time_from
  attr_accessor :time_to
  def to_s
      d = self
      res = d.name_source.to_s + "  " +
            d.name_destination.to_s + "  " +
            d.command.to_s + "  " +
            d.level.to_s + "  " +
            d.time_from.day.to_s + "." + d.time_from.month.to_s + "." + d.time_from.year.to_s + " " +
            d.time_from.hour.to_s + ":" +  d.time_from.min.to_s + ":" + d.time_from.sec.to_s + "  " +
            d.time_to.day.to_s + "." + d.time_to.month.to_s + "." + d.time_to.year.to_s + " " +
            d.time_to.hour.to_s + ":" +  d.time_to.min.to_s + ":" + d.time_to.sec.to_s
  end
  def do_c
    source = File.readlines(name_source)

    source = source.map { |el| ParserSystemData.createData(el) }.select { |x| !x.nil? }
    source = source.select { 
      |el|
      res = false
      if ((el.time <=> time_to) != 1)
        if ((time_from <=> el.time) != 1)
          res = true
        end
      end
      res
    }
    if (level != "any")
      source = source.select { 
        |el|
        el.level == level
      }
    end
    
    if (command == "count")
      File.write(name_destination, "count: " + source.count.to_s)
    end
    if (command == "print")
      File.write(name_destination, source.inject {|sum, n| sum + "\n"+ n.to_s } )
    end
    puts source
  end
end

class ParserCommand
  @@rx =
    /
      ^
      ([а-яА-Я\.\w]+)           #name_source
      \s+
      ([а-яА-Я\.\w]+)           #name_destination
      \s+
      ([а-яА-Я\w]+)           #command
      \s+
      ([а-яА-Я\w]+)           #level
      \s+                   #FROM
      (\d{1,2})             #time.day
      \.
      (\d{1,2})             #time.month
      \.
      (\d{2,4})             #time.year
      \s+
      (\d{1,2})             #time.hour
      \:
      (\d{1,2})             #time.min
      \:
      (\d{1,2})             #time.sec
      \s+                   #TO
      (\d{1,2})             #time.day
      \.
      (\d{1,2})             #time.month
      \.
      (\d{2,4})             #time.year
      \s+
      (\d{1,2})             #time.hour
      \:
      (\d{1,2})             #time.min
      \:
      (\d{1,2})             #time.sec
      $
    /x
    def self.parse(commandString)
      @@rx.match(commandString)
    end
  
    def self.createCommand(commandString)
      res = parse(commandString)
      data = nil
      if (res != nil)
        data = Command.new()
        data.name_source = res.captures[0]
        data.name_destination = res.captures[1]
        data.command = res.captures[2]
        data.level = res.captures[3]
        i = 4
        data.time_from = Time.new(res.captures[2+i], res.captures[1+i], res.captures[i], res.captures[3+i], res.captures[4+i], res.captures[5+i])
        i = 10
        data.time_to = Time.new(res.captures[2+i], res.captures[1+i], res.captures[i], res.captures[3+i], res.captures[4+i], res.captures[5+i])
        data
      end 
      data
    end
end

lines = File.readlines("commands.txt")
commands = lines.map { |el| ParserCommand.createCommand(el) }.select { |x| !x.nil? }
commands.each { |el| el.do_c }

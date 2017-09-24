g_arr = ('a'..'g').map{|obj| obj}
num = 5


def is_last_equal_char(word, char)
  word[word.length-1] == char
end

def my_pre_map(pre, post_arr)
  post_arr.map { |last| ( pre+last if !is_last_equal_char(pre,last) ) }.compact
end

def my_multiply_arr(pre_arr, post_arr)
  pre_arr.map { |pre| my_pre_map(pre, post_arr) }.flatten
end


l_arr = g_arr
l_arr =  (2..num).inject(l_arr) {|pre_arr, trash_buf| my_multiply_arr(pre_arr, g_arr) }
puts l_arr

puts "Count elements: " + l_arr.count.to_s
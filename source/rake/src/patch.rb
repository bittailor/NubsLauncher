# Patch Rake Retry Patch
module Rake

   # Patch Rake so we can retry each target on a fail
   class Task

      alias_method :org_execute , :execute

      def execute(args=nil)
         begin
            org_execute(args)
         rescue
            puts ""
            puts ""
            puts "#{name} failed with : #{$!.to_s}"
            print $!.backtrace.join("\n")
            puts ""
            puts ""
            answer = nil
            begin
               puts "--> Try build #{name} again (y)es (n)o or just (i)gnore it and continue? [y|n|i]>"
               $stdout.flush
               answer = $stdin.gets
               answer.strip!
            end while !["y","n","i"].include?(answer)
            if answer == "y"
               puts "#{answer} -> retry"
               retry
            elsif answer == "i"
               puts "#{answer} -> ignore"
            else
               puts "#{answer} -> fail"
               raise $!
            end
         end
      end
   end
end

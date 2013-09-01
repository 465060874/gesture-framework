package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;

/**
 * {@link TypeData} manager for {@link Processor} objects to delegate to.
 */
public class TypeDataManager<T extends Processor> {
//    @Getter @NonNull private TypeData typeData;
//    private final T managedProcessor;
//
//    public TypeDataManager(T managedProcessor) {
//        this.typeData = TypeData.getDefaultType();
//        this.managedProcessor = managedProcessor;
//    }
//
//    public TypeDataManager(T managedProcessor, TypeData typeData) {
//        this.typeData = typeData;
//        this.managedProcessor = managedProcessor;
//    }
//
//    @SuppressWarnings("unchecked")
//    public T withTypeData(TypeData typeData) {
//        if (managedProcessor.isMutable()) {
//            this.typeData = typeData;
//            return managedProcessor;
//        } else {
//            T processorClone = (T) managedProcessor.createMutableClone();
//            processorClone.withTypeData(typeData);
//            return processorClone;
//        }
//    }
}

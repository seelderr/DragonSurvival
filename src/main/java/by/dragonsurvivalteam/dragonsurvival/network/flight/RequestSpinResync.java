package by.dragonsurvivalteam.dragonsurvival.network.flight;


// TODO: Sort out what this is supposed to do
/*public class RequestSpinResync implements IMessage<RequestSpinResync> {
    public RequestSpinResync() {}

    @Override
    public void encode(final RequestSpinResync message, FriendlyByteBuf buffer) { }

    @Override
    public RequestSpinResync decode(final FriendlyByteBuf buffer) {
        return new RequestSpinResync();
    }

    @Override
    public void handle(final RequestSpinResync message, final Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer sender = context.getSender();

        if (sender != null) {
            DragonStateProvider.getCap(sender).ifPresent(handler -> NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncSpinStatus(sender.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned)));
        }

        context.setPacketHandled(true);
    }
}*/